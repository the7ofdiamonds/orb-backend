CREATE DEFINER=`root`@`%` PROCEDURE `findUserByEmail`(
    IN p_user_email VARCHAR(255)
)
BEGIN
    SELECT JSON_OBJECT(
        'id', u.ID,
        'email', u.user_email,
        'username', u.display_name,
        'password', u.user_pass,
        'phone', MAX(CASE WHEN m.meta_key = 'phone_number' THEN m.meta_value END),
        'firstname', MAX(CASE WHEN m.meta_key = 'first_name' THEN m.meta_value END),
        'lastname', MAX(CASE WHEN m.meta_key = 'last_name' THEN m.meta_value END),
		'roles', JSON_ARRAY(MAX(CASE WHEN m.meta_key = 'wp_capabilities' THEN m.meta_value END)),
        'description', MAX(CASE WHEN m.meta_key = 'description' THEN m.meta_value END),
        'providerGivenID', MAX(CASE WHEN m.meta_key = 'providerGivenID' THEN m.meta_value END),
        'isAuthenticated', MAX(CASE WHEN m.meta_key = 'isAuthenticated' THEN m.meta_value END),
        'isAccountNonExpired', MAX(CASE WHEN m.meta_key = 'isAccountNonExpired' THEN m.meta_value END),
        'isAccountNonLocked', MAX(CASE WHEN m.meta_key = 'isAccountNonLocked' THEN m.meta_value END),
        'isCredentialsNonExpired', MAX(CASE WHEN m.meta_key = 'isCredentialsNonExpired' THEN m.meta_value END),
        'isEnabled', MAX(CASE WHEN m.meta_key = 'isEnabled' THEN m.meta_value END)
    ) AS user_info
    FROM wordpress.wp_users u
    LEFT JOIN wordpress.wp_usermeta m ON u.ID = m.user_id
    WHERE u.user_email COLLATE utf8mb4_unicode_520_ci = p_user_email
    GROUP BY u.ID, u.user_login, u.user_email;
END