USE `wordpress`;
DROP procedure IF EXISTS `addNewUser`;

USE `wordpress`;
DROP procedure IF EXISTS `wordpress`.`addNewUser`;
;

DELIMITER $$
USE `wordpress`$$

CREATE DEFINER=`root`@`%` PROCEDURE `addNewUser`(IN p_user_email VARCHAR(255), IN p_display_name VARCHAR(255), IN p_user_pass VARCHAR(255), IN p_first_name VARCHAR(255), IN p_last_name VARCHAR(255), IN p_phone_number VARCHAR(255))
BEGIN
    -- Insert user into wp_users table
    INSERT INTO wp_users (user_email, user_login, display_name, user_pass, user_registered)
    VALUES (p_user_email, p_user_email, p_display_name, p_user_pass, NOW());

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
END

DELIMITER ;
;