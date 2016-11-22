package com.advent.service;

import com.advent.dto.UserDTO;
import com.advent.entity.UserGroup;
import com.advent.repo.GroupRepo;
import com.advent.repo.UserGroupRepo;
import com.advent.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

/**
 * Created by clai on 10/28/16.
 */
@Service
public class UserGroupService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GroupRepo groupRepo;
    @Autowired
    private UserGroupRepo userGroupRepo;

    
    public UserGroup joinGroupAsUser(Long userId, Long groupId, String role) {

        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        UserDTO currentUser = (UserDTO) a.getDetails();

        if(userGroupRepo.findByUserIdAndGroupId(userId, groupId).getRole() == "MODERATOR") {
            UserGroup userGroup = new UserGroup();
            userGroup.setUser(userRepo.findOne(userId));
            userGroup.setGroup(groupRepo.findOne(groupId));
            userGroup.setRole(role);
            return userGroupRepo.save(userGroup);
        }
        return null;
    }

    /**
     * Same as joinGroupAsUser but adds the user in the security context instead.
     * @param groupId
     * @param role
     */
    public UserGroup joinGroup(Long groupId, String role) {
        UserDTO currentUser = null;

        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        currentUser = (UserDTO) a.getDetails();

        //return null if this usergroup already exists.
        if(userGroupRepo.findByUserIdAndGroupId(currentUser.getId(), groupId) != null) return null;

        UserGroup userGroup = new UserGroup();
        userGroup.setUser(userRepo.findOne(currentUser.getId()));

        userGroup.setGroup(groupRepo.findOne(groupId));
        userGroup.setRole(role);
        return userGroupRepo.save(userGroup);
    }

    public UserGroup getUserGroup(Long userId, Long groupId) {
        return userGroupRepo.findByUserIdAndGroupId(userId, groupId);
    }

    public UserGroup changeUserRoleForGroup(Long userGroupId, String newRole) {
        UserDTO currentUser = null;

        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        currentUser = (UserDTO) a.getDetails();

        UserGroup userGroup = userGroupRepo.findOne(userGroupId);

        //Don't allow changing role of admins
        if(userGroup.getRole() == "ADMIN")
            return null;

        //make sure logged in user is Admin of group.
        if(userGroupRepo.findByUserIdAndGroupId(currentUser.getId(), userGroup.getGroup().getId()).getRole() != "ADMIN")
            return null;

        userGroup.setRole(newRole);
        return userGroupRepo.save(userGroup);
    }
}

























