CREATE DEFINER=`root`@`%` PROCEDURE `existsByNicename`(
    IN p_nicename VARCHAR(255)
)
BEGIN
    DECLARE user_count INT;
    
    SET user_count = 0;
    
    SELECT COUNT(*) 
    INTO user_count
    FROM wordpress.wp_users AS u
    WHERE u.user_nicename = p_nicename COLLATE utf8mb4_unicode_ci;

    SELECT CASE WHEN user_count >= 1 THEN 'TRUE' ELSE 'FALSE' END AS resultSet;
END