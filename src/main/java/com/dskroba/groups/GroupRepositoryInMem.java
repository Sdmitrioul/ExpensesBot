package com.dskroba.groups;

import com.dskroba.base.exception.RepositoryException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GroupRepositoryInMem implements GroupRepository {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private final AtomicLong idGenerator = new AtomicLong();
    private final Map<Long, Group> groups = new HashMap<>();

    @Override
    public List<Group> getPrincipalGroups(Long principalId) throws RepositoryException {
        readLock.lock();
        try {
            return groups.values().stream().filter(group -> group.owner().equals(principalId)).toList();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Group saveGroup(Group group) throws RepositoryException {
        writeLock.lock();
        try {
            if (group.id() == null) {
                group = Group.Builder.from(group).id(idGenerator.incrementAndGet()).build();
            }
            groups.put(group.id(), group);
            return group;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void deleteGroup(Long groupId) throws RepositoryException {
        writeLock.lock();
        try {
            groups.remove(groupId);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void leaveGroup(Long groupId, Long principalId) throws RepositoryException {
        writeLock.lock();
        try {
            Group group = groups.get(groupId);
            if (group == null) {
                return;
            }
            group = Group.Builder.from(group).removeMember(principalId).build();
            groups.put(groupId, group);
        } finally {
            writeLock.unlock();
        }
    }
}
