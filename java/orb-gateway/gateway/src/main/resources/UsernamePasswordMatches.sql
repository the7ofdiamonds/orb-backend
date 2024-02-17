CREATE DEFINER=`root`@`%` PROCEDURE `usernamePasswordMatches`(
    IN p_display_name VARCHAR(255),
    IN p_user_pass VARCHAR(255)
)
BEGIN
    SELECT JSON_OBJECT(
        'username', u.display_name,
        'password', u.user_pass
    ) AS user_info
    FROM wordpress.wp_users u
    WHERE u.display_name COLLATE utf8mb4_unicode_520_ci = p_display_name
	AND u.user_pass COLLATE utf8mb4_unicode_520_ci = p_user_pass;
END