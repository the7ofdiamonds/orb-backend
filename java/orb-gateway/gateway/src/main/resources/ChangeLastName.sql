CREATE DEFINER=`root`@`%` PROCEDURE `changeLastName`(
    IN p_user_email VARCHAR(255), 
    IN p_user_pass VARCHAR(255), 
	IN p_last_name_new VARCHAR(255)
)
BEGIN
    DECLARE user_id INT;

SELECT 
    u.ID
INTO user_id FROM
    wordpress.wp_users u
WHERE
    u.user_email COLLATE utf8mb4_unicode_520_ci = p_user_email
        AND u.user_pass COLLATE utf8mb4_unicode_520_ci = p_user_pass;

    IF user_id IS NOT NULL THEN
        CALL addUserMeta(user_id, 'last_name', p_last_name_new);
    END IF;
    
	SELECT 
    CASE
        WHEN ROW_COUNT() > 0 THEN 'TRUE'
        ELSE 'FALSE'
    END AS resultSet;
END