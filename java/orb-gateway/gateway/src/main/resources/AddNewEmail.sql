CREATE DEFINER=`root`@`%` PROCEDURE `addNewEmail`(
    IN p_user_email VARCHAR(255), 
    IN p_display_name VARCHAR(255), 
	IN p_user_email_new VARCHAR(255)
)
BEGIN
    DECLARE user_id INT;

SELECT 
    u.ID
INTO user_id FROM
    wordpress.wp_users u
WHERE
    u.user_email COLLATE utf8mb4_unicode_520_ci = p_user_email
        AND u.display_name COLLATE utf8mb4_unicode_520_ci = p_display_name;

    IF user_id IS NOT NULL THEN
        CALL addUserMeta(user_id, 'additional_email', p_user_email_new);
    END IF;
    
	SELECT 
    CASE
        WHEN ROW_COUNT() > 0 THEN 'TRUE'
        ELSE 'FALSE'
    END AS resultSet;
END