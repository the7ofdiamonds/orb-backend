CREATE DEFINER=`root`@`%` PROCEDURE `createAccount`(
    IN p_user_email VARCHAR(255), 
    IN p_display_name VARCHAR(255), 
    IN p_user_pass VARCHAR(255),
    IN p_user_nicename VARCHAR(255),
    IN p_nickname VARCHAR(255),
    IN p_first_name VARCHAR(255), 
    IN p_last_name VARCHAR(255), 
    IN p_phone_number VARCHAR(255),
    IN p_user_activation_key VARCHAR(255)
)
BEGIN
	START TRANSACTION;
    
    INSERT INTO wp_users (user_email, user_login, display_name, user_pass, user_nicename, user_activation_key, user_registered)
    VALUES (p_user_email, p_user_email, p_display_name, p_user_pass, p_user_nicename, p_user_activation_key, NOW());

    SET @user_id = LAST_INSERT_ID();

        CALL addUserMeta(@user_id, 'first_name', p_first_name);
        CALL addUserMeta(@user_id, 'last_name', p_last_name);
        CALL addUserMeta(@user_id, 'phone_number', p_phone_number);
		CALL addUserMeta(@user_id, 'nickname', p_nickname);
        CALL addUserMeta(@user_id, 'is_authenticated', 0);
        CALL addUserMeta(@user_id, 'is_account_non_expired', 0);
        CALL addUserMeta(@user_id, 'is_account_non_locked', 1);
        CALL addUserMeta(@user_id, 'is_credentials_non_expired', 1);
		CALL addUserMeta(@user_id, 'is_enabled', 1);

    CALL findUserByID(@user_id);
    
    COMMIT;
END