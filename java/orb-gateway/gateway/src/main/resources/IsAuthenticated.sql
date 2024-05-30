CREATE DEFINER=`root`@`%` PROCEDURE `isAuthenticated`(
    IN p_user_email VARCHAR(255)
)
BEGIN
    DECLARE user_id INT;

    SELECT 
        u.ID
    INTO user_id 
    FROM
        wordpress.wp_users u
    LEFT JOIN
        wordpress.wp_usermeta m ON u.ID = m.user_id
    WHERE
        u.user_email COLLATE utf8mb4_unicode_520_ci = p_user_email
    LIMIT 1;

    IF user_id IS NOT NULL THEN
        CALL addUserMeta(user_id, 'is_authenticated', 1);
    END IF;
    
    SELECT 
        CASE
            WHEN ROW_COUNT() > 0 THEN 'TRUE'
            ELSE 'FALSE'
        END AS resultSet;
END