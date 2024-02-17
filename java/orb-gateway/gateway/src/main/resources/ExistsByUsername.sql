CREATE DEFINER=`root`@`%` PROCEDURE `existsByUsername`(
    IN p_display_name VARCHAR(255)
)
BEGIN
    SELECT COUNT(*) as user_count
    FROM wordpress.wp_users AS u
    WHERE u.display_name = p_display_name COLLATE utf8mb4_unicode_ci;
END