CREATE DEFINER=`root`@`%` PROCEDURE `existsByEmail`(
    IN p_user_email VARCHAR(255)
)
BEGIN
    SELECT COUNT(*) as user_count
    FROM wordpress.wp_users AS u
    WHERE u.user_email = p_user_email COLLATE utf8mb4_unicode_ci;
END