package com.api.validatejwt.v1.enums;

import java.util.Arrays;

public enum EnumRole {
	Admin, Member, External;
	
    @Override
    public String toString() {
        return name().toUpperCase();
    }

	public static boolean isValidRole(String role) {
	    return Arrays.stream(EnumRole.values())
	                 .anyMatch(enumRole -> enumRole.name().equals(role));
	}
}
