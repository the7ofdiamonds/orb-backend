CREATE DEFINER=`root`@`%` PROCEDURE `addUserMeta`(
    IN p_user_id BIGINT,
    IN p_meta_key VARCHAR(255),
    IN p_meta_value LONGTEXT
)
BEGIN
	DECLARE record_count INT;
    
    START TRANSACTION;

    SELECT COUNT(*)
    INTO record_count
    FROM wp_usermeta
    WHERE user_id = p_user_id AND meta_key COLLATE utf8mb4_unicode_ci = p_meta_key COLLATE utf8mb4_unicode_ci;

    IF record_count > 0 THEN
        UPDATE wp_usermeta
        SET meta_value = p_meta_value
        WHERE user_id = p_user_id AND meta_key COLLATE utf8mb4_unicode_ci = p_meta_key COLLATE utf8mb4_unicode_ci;
    ELSE 
        INSERT INTO wp_usermeta (user_id, meta_key, meta_value)
        VALUES
            (p_user_id, p_meta_key, p_meta_value);
    END IF;

    COMMIT;
END