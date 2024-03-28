CREATE DEFINER=`root`@`%` PROCEDURE `deleteAccount`(
    IN p_user_email VARCHAR(255)
)
BEGIN
    DECLARE userID INT;

    SELECT u.ID INTO userID 
    FROM wordpress.wp_users u
    WHERE u.user_email COLLATE utf8mb4_unicode_520_ci = p_user_email;

    IF userID IS NOT NULL THEN
        DELETE FROM wordpress.wp_usermeta
        WHERE user_id = userID;
        
        DELETE FROM wordpress.wp_users 
        WHERE ID = userID;

        SELECT 'TRUE' AS result;
    ELSE
        SELECT 'FALSE' AS result;
    END IF;
END