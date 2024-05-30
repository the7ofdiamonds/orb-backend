CREATE DEFINER=`root`@`%` PROCEDURE `existsByPhone`(
    IN p_phone_number VARCHAR(255)
)
BEGIN
    DECLARE user_count INT;
    
    SET user_count = 0;
    
    SELECT COUNT(*) 
    INTO user_count
    FROM wp_usermeta AS m
    WHERE m.meta_key = 'phone_number' AND m.meta_value = p_phone_number COLLATE utf8mb4_unicode_ci;

    SELECT CASE WHEN user_count >= 1 THEN 'TRUE' ELSE 'FALSE' END AS resultSet;
END