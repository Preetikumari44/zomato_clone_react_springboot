package com.novabyte.zomatoclone.common.enums;

/**
 * The four roles a user can hold. A single user may be assigned more than
 * one (see UserRole) but only ever authenticates as ONE active role per
 * JWT — see security/JwtTokenProvider for why.
 */
public enum Role {
    CUSTOMER,
    RESTAURANT_OWNER,
    DELIVERY_PARTNER,
    ADMIN
}
