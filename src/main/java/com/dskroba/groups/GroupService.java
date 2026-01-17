package com.dskroba.groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    private final GroupRepository repository;

    @Autowired
    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    public List<Group> getPrincipalGroups(Long principalId) {
        return repository.getPrincipalGroups(principalId);
    }

    public void createGroup(Group group) {
        repository.saveGroup(group);
    }

    public void leaveGroup(Long groupId, Long principalId) {
        repository.leaveGroup(groupId, principalId);
    }
}
