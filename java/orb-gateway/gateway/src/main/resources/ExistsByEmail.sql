CREATE DEFINER=`root`@`%` PROCEDURE `findUserByEmail`(
    IN p_user_email VARCHAR(255)
)
BEGIN
    SELECT 
		u.ID AS id,
		u.user_email AS email,
        u.display_name AS username,
        u.user_pass AS password,
		MAX(CASE WHEN m.meta_key = 'phone_number' THEN m.meta_value END) AS phone,
        MAX(CASE WHEN m.meta_key = 'first_name' THEN m.meta_value END) AS firstname,
        MAX(CASE WHEN m.meta_key = 'last_name' THEN m.meta_value END) AS lastname,
        MAX(CASE WHEN m.meta_key = 'provider_given_id' THEN m.meta_value END) AS providerGivenID,
		MAX(CASE WHEN m.meta_key = 'confirmation_code' THEN m.meta_value END) AS confirmationCode,
        MAX(CASE WHEN m.meta_key = 'is_authenticated' THEN m.meta_value END) AS isAuthenticated,
        MAX(CASE WHEN m.meta_key = 'is_account_non_expired' THEN m.meta_value END) AS isAccountNonExpired,
        MAX(CASE WHEN m.meta_key = 's_account_non_locked' THEN m.meta_value END) AS isAccountNonLocked,
        MAX(CASE WHEN m.meta_key = 'is_credentials_non_expired' THEN m.meta_value END) AS isCredentialsNonExpired,
        MAX(CASE WHEN m.meta_key = 'is_enabled' THEN m.meta_value END) AS isEnabled,
        JSON_ARRAY(MAX(CASE WHEN m.meta_key = 'wp_capabilities' THEN m.meta_value END)) AS roles
    FROM wordpress.wp_users u
    LEFT JOIN wordpress.wp_usermeta m ON u.ID = m.user_id
    WHERE u.user_email COLLATE utf8mb4_unicode_520_ci = p_user_email 
    GROUP BY u.ID, u.user_login, u.user_email;
END