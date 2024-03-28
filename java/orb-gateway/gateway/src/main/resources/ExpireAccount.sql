CREATE DEFINER=`root`@`%` PROCEDURE `expireAccount`(
    IN p_user_email VARCHAR(255), 
    IN p_user_pass VARCHAR(255)
)
BEGIN
    DECLARE user_id INT;

SELECT 
    u.ID
INTO user_id FROM
    wordpress.wp_users u
        LEFT JOIN
    wordpress.wp_usermeta m ON u.ID = m.user_id
WHERE
    u.user_email COLLATE utf8mb4_unicode_520_ci = p_user_email
        AND u.user_pass COLLATE utf8mb4_unicode_520_ci = p_user_pass;
        
    IF user_id IS NOT NULL THEN
        CALL addUserMeta(user_id, 'is_account_non_expired', false);
    END IF;
    
	SELECT 
    CASE
        WHEN ROW_COUNT() > 0 THEN 'TRUE'
        ELSE 'FALSE'
    END AS resultSet;
END