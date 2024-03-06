CREATE DEFINER=`root`@`%` PROCEDURE `findUserByID`(
    IN p_ID LONG
)
BEGIN
    SELECT 
        u.ID AS id,
        u.user_email AS email,
        u.display_name AS username,
        u.user_pass AS password,
        MAX(CASE WHEN m.meta_key = 'phone_number' THEN m.meta_value END) AS phone,
        MAX(CASE WHEN m.meta_key = 'first_name' THEN m.meta_value END) AS first_name,
        MAX(CASE WHEN m.meta_key = 'last_name' THEN m.meta_value END) AS last_name,
        JSON_ARRAY(MAX(CASE WHEN m.meta_key = 'wp_capabilities' THEN m.meta_value END)) AS roles,
        MAX(CASE WHEN m.meta_key = 'description' THEN m.meta_value END) AS bio,
        MAX(CASE WHEN m.meta_key = 'providerGivenID' THEN m.meta_value END) AS provider_given_id,
        MAX(CASE WHEN m.meta_key = 'isAuthenticated' THEN m.meta_value END) AS is_authenticated,
        MAX(CASE WHEN m.meta_key = 'isAccountNonExpired' THEN m.meta_value END) AS is_account_non_expired,
        MAX(CASE WHEN m.meta_key = 'isAccountNonLocked' THEN m.meta_value END) AS is_account_non_locked,
        MAX(CASE WHEN m.meta_key = 'isCredentialsNonExpired' THEN m.meta_value END) AS is_credentials_non_expired,
        MAX(CASE WHEN m.meta_key = 'isEnabled' THEN m.meta_value END) AS is_enabled,
        MAX(CASE WHEN m.meta_key = 'confirmation_code' THEN m.meta_value END) AS confirmationCode
        
    FROM wordpress.wp_users u
    LEFT JOIN wordpress.wp_usermeta m ON u.ID = m.user_id
    WHERE u.ID COLLATE utf8mb4_unicode_520_ci = p_ID;
END