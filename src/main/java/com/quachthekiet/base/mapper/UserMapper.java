package com.quachthekiet.base.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.quachthekiet.base.dto.UserDTO;
import com.quachthekiet.base.model.Role;
import com.quachthekiet.base.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToNames")
    @Mapping(target = "permissions", source = "roles", qualifiedByName = "mapPermissionsToNames")
    UserDTO toDTO(User user);

    @Named("mapRolesToNames")
    default List<String> mapRolesToNames(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
    }

    @Named("mapPermissionsToNames")
    default List<String> mapPermissionsToNames(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .distinct()
                .collect(Collectors.toList());
    }
}
