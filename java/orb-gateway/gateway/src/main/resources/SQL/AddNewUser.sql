USE `wordpress`;
DROP procedure IF EXISTS `addNewUser`;

DELIMITER $$
USE `wordpress`$$
CREATE PROCEDURE addNewUser(IN p_user_login VARCHAR(255), IN p_user_pass VARCHAR(255), IN p_user_email VARCHAR(255), IN p_first_name VARCHAR(255), IN p_last_name VARCHAR(255), IN p_phone_number VARCHAR(255))
BEGIN
    -- Insert user into wp_users table
    INSERT INTO wp_users (user_login, user_pass, user_email, user_registered)
    VALUES (p_user_login, MD5(p_user_pass), p_user_email, NOW());

    -- Get the user ID of the last inserted user
    SET @user_id = LAST_INSERT_ID();

    -- Insert user meta information
    INSERT INTO wp_usermeta (user_id, meta_key, meta_value)
    VALUES
        (@user_id, 'first_name', p_first_name),
        (@user_id, 'last_name', p_last_name),
        (@user_id, 'phone_number', p_phone_number);

    -- Return the user ID
SELECT @user_id AS user_id;
END$$

DELIMITER ;

