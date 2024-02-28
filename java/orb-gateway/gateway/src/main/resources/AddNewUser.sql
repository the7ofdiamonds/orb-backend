CREATE DEFINER=`root`@`%` PROCEDURE `addNewUser`(
    IN p_user_email VARCHAR(255), 
    IN p_display_name VARCHAR(255), 
    IN p_user_pass VARCHAR(255), 
    IN p_first_name VARCHAR(255), 
    IN p_last_name VARCHAR(255), 
    IN p_phone_number VARCHAR(255)
)
BEGIN
    INSERT INTO wp_users (user_email, user_login, display_name, user_pass, user_registered)
    VALUES (p_user_email, p_user_email, p_display_name, p_user_pass, NOW());

    SET @user_id = LAST_INSERT_ID();

    INSERT INTO wp_usermeta (user_id, meta_key, meta_value)
    VALUES
        (@user_id, 'first_name', p_first_name),
        (@user_id, 'last_name', p_last_name),
        (@user_id, 'phone_number', p_phone_number);

    CALL findUserByID(@user_id);
END