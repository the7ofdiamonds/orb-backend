CREATE DEFINER=`root`@`%` PROCEDURE `existsByUsername`(
    IN p_display_name VARCHAR(255)
)
BEGIN
    DECLARE user_count INT;
    
    SELECT COUNT(*) 
    INTO user_count
    FROM wordpress.wp_users AS u
    WHERE u.display_name = p_display_name COLLATE utf8mb4_unicode_ci;

    SELECT CASE WHEN user_count = 1 THEN 'TRUE' ELSE 'FALSE' END AS resultSet;
END