CREATE DEFINER=`root`@`%` PROCEDURE `addNewUser`(
    IN p_user_email VARCHAR(255), 
    IN p_display_name VARCHAR(255), 
    IN p_user_pass VARCHAR(255), 
    IN p_first_name VARCHAR(255), 
    IN p_last_name VARCHAR(255), 
    IN p_phone_number VARCHAR(255),
    IN p_roles VARCHAR(255),
    IN p_provider_given_id VARCHAR(255),
    IN p_is_authenticated BOOLEAN,
    IN p_is_account_non_expired BOOLEAN,
    IN p_is_account_non_locked BOOLEAN,
    IN p_is_credentials_non_expired BOOLEAN,
    IN p_is_enabled BOOLEAN
)
BEGIN
	START TRANSACTION;
    
    INSERT INTO wp_users (user_email, user_login, display_name, user_pass, user_registered)
    VALUES (p_user_email, p_user_email, p_display_name, p_user_pass, NOW());

    SET @user_id = LAST_INSERT_ID();

        CALL addUserMeta(@user_id, 'first_name', p_first_name);
        CALL addUserMeta(@user_id, 'last_name', p_last_name);
        CALL addUserMeta(@user_id, 'phone_number', p_phone_number);
        CALL addUserMeta(@user_id, 'wp_capabilities', p_roles);
        CALL addUserMeta(@user_id, 'provider_given_id', p_provider_given_id);
        CALL addUserMeta(@user_id, 'is_authenticated', p_is_authenticated);
        CALL addUserMeta(@user_id, 'is_account_non_expired', p_is_account_non_expired);
        CALL addUserMeta(@user_id, 'is_account_non_locked', p_is_account_non_locked);
        CALL addUserMeta(@user_id, 'is_credentials_non_expired', p_is_credentials_non_expired);
		CALL addUserMeta(@user_id, 'is_enabled', p_is_enabled);

    CALL findUserByID(@user_id);
    
    COMMIT;
END