CREATE DEFINER=`root`@`%` PROCEDURE `findUserByID`(
    IN p_ID LONG
)
BEGIN
    SELECT * FROM wordpress.user_details_view
    WHERE id COLLATE utf8mb4_unicode_520_ci = p_ID;
END