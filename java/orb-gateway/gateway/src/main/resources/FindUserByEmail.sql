CREATE DEFINER=`root`@`%` PROCEDURE `findUserByEmail`(
    IN p_user_email VARCHAR(255)
)
BEGIN
    SELECT * FROM wordpress.user_details_view
    WHERE email COLLATE utf8mb4_unicode_520_ci = p_user_email; 
END