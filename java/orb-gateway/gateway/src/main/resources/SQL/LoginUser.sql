USE `wordpress`;
DROP procedure IF EXISTS `loginUser`;

DELIMITER $$
USE `wordpress`$$
CREATE DEFINER=`root`@`%` PROCEDURE `loginUser` (IN p_user_login VARCHAR(255), IN p_user_pass VARCHAR(255))
BEGIN
    SELECT u.ID AS id, u.user_email AS email, u.display_name AS username, u.user_pass AS password,
    	MAX(CASE WHEN m.meta_key = 'phone_number' THEN m.meta_value END) AS phone,
        MAX(CASE WHEN m.meta_key = 'first_name' THEN m.meta_value END) AS firstname,
    	MAX(CASE WHEN m.meta_key = 'last_name' THEN m.meta_value END) AS lastname,
    	MAX(CASE WHEN m.meta_key = 'wp_capabilities' THEN m.meta_value END) AS roles,
    	MAX(CASE WHEN m.meta_key = 'description' THEN m.meta_value END) AS description,
    	MAX(CASE WHEN m.meta_key = 'providerGivenID' THEN m.meta_value END) AS providerGivenID,
    	MAX(CASE WHEN m.meta_key = 'isAuthenticated' THEN m.meta_value END) AS isAuthenticated,
    	MAX(CASE WHEN m.meta_key = 'isAccountNonExpired' THEN m.meta_value END) AS isAccountNonExpired,
    	MAX(CASE WHEN m.meta_key = 'isAccountNonLocked' THEN m.meta_value END) AS isAccountNonLocked,
    	MAX(CASE WHEN m.meta_key = 'isCredentialsNonExpired' THEN m.meta_value END) AS isCredentialsNonExpired,
    	MAX(CASE WHEN m.meta_key = 'isEnabled' THEN m.meta_value END) AS isEnabled
    FROM wordpress.wp_users u
    LEFT JOIN wordpress.wp_usermeta m ON u.ID = m.user_id
    WHERE u.user_login = p_user_login AND u.user_pass = p_user_pass
    GROUP BY u.ID, u.user_login, u.user_email;
END ;$$

DELIMITER ;