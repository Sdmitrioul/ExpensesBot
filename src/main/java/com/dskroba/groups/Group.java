package com.dskroba.groups;

import java.util.HashSet;
import java.util.Set;

public record Group(Long id, String name, Long owner, Set<Long> members) {
    public boolean hasDiff(Group group) {
        return !(name.equals(group.name()) && owner.equals(group.owner()));
    }

    public boolean haveSameMembers(Group group) {
        return members.containsAll(group.members()) && group.members().containsAll(members);
    }

    public static class Builder {
        private Long id;
        private String name;
        private Long ownerId;
        private Set<Long> members = new HashSet<>();

        public static Builder from(Group group) {
            return new Builder()
                    .id(group.id())
                    .name(group.name())
                    .ownerId(group.owner())
                    .members(new HashSet<>(group.members()));
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder ownerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder members(Set<Long> members) {
            this.members = members != null ? new HashSet<>(members) : new HashSet<>();
            return this;
        }

        public Builder addMember(Long memberId) {
            this.members.add(memberId);
            return this;
        }

        public Builder removeMember(Long memberId) {
            this.members.remove(memberId);
            return this;
        }

        public Group build() {
            return new Group(id, name, ownerId, members);
        }
    }
}
