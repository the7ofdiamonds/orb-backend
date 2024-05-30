CREATE DEFINER=`root`@`%` PROCEDURE `existsByEmail`(
    IN p_user_email VARCHAR(255)
)
BEGIN
    DECLARE user_count INT;

    SELECT COUNT(*)
    INTO user_count
    FROM wordpress.wp_users AS u
    WHERE u.user_email = p_user_email COLLATE utf8mb4_unicode_ci;
    
    SELECT CASE WHEN user_count = 1 THEN 'TRUE' ELSE 'FALSE' END AS resultSet;
END