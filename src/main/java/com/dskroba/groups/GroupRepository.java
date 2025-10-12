package com.dskroba.groups;

import com.dskroba.base.exception.RepositoryException;

import java.util.List;

public interface GroupRepository {
    List<Group> getPrincipalGroups(Long principalId) throws RepositoryException;

    Group saveGroup(Group group) throws RepositoryException;

    void deleteGroup(Long groupId) throws RepositoryException;

    void leaveGroup(Long groupId, Long principalId) throws RepositoryException;
}
