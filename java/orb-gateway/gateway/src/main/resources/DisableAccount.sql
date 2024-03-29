CREATE DEFINER=`root`@`%` PROCEDURE `disableAccount`(
    IN p_user_email VARCHAR(255), 
    IN p_user_pass VARCHAR(255)
)
BEGIN
    DECLARE user_id INT;
    DECLARE affected_rows INT;

SELECT 
    u.ID
INTO user_id FROM
    wordpress.wp_users u
WHERE
    u.user_email COLLATE utf8mb4_unicode_520_ci = p_user_email
        AND u.user_pass COLLATE utf8mb4_unicode_520_ci = p_user_pass
LIMIT 1;

    IF user_id IS NOT NULL THEN
        START TRANSACTION;

UPDATE wordpress.wp_usermeta 
SET 
    meta_value = 0
WHERE
    user_id = user_id
        AND meta_key = 'is_enabled';

        SET affected_rows = ROW_COUNT();

        IF affected_rows > 0 THEN
            COMMIT;
SELECT 'TRUE' AS resultSet;
        ELSE
            ROLLBACK;
SELECT 'FALSE' AS resultSet;
        END IF;
    ELSE
        SELECT 'FALSE' AS resultSet;
    END IF;
END