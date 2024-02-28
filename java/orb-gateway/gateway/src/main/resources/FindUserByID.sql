CREATE DEFINER=`root`@`%` PROCEDURE `findUserByID`(
    IN p_ID LONG
)
BEGIN
    SELECT
        u.ID AS id,
		u.user_email,
        u.display_name,
        u.user_pass,
		MAX(CASE WHEN m.meta_key = 'phone_number' THEN m.meta_value END),
        MAX(CASE WHEN m.meta_key = 'first_name' THEN m.meta_value END),
        MAX(CASE WHEN m.meta_key = 'last_name' THEN m.meta_value END),
		JSON_ARRAY(MAX(CASE WHEN m.meta_key = 'wp_capabilities' THEN m.meta_value END)),
        MAX(CASE WHEN m.meta_key = 'description' THEN m.meta_value END),
        MAX(CASE WHEN m.meta_key = 'providerGivenID' THEN m.meta_value END),
        MAX(CASE WHEN m.meta_key = 'isAuthenticated' THEN m.meta_value END),
        MAX(CASE WHEN m.meta_key = 'isAccountNonExpired' THEN m.meta_value END),
        MAX(CASE WHEN m.meta_key = 'isAccountNonLocked' THEN m.meta_value END),
        MAX(CASE WHEN m.meta_key = 'isCredentialsNonExpired' THEN m.meta_value END),
        MAX(CASE WHEN m.meta_key = 'isEnabled' THEN m.meta_value END)
    FROM wordpress.wp_users u
    LEFT JOIN wordpress.wp_usermeta m ON u.ID = m.user_id
    WHERE u.ID COLLATE utf8mb4_unicode_520_ci = p_ID
    GROUP BY u.ID, u.user_login, u.user_email;
END