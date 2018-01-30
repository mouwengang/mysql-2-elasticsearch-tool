DROP FUNCTION getParentList;
create FUNCTION getParentList(rootId varchar(1000))
RETURNS VARCHAR(1000)
BEGIN
DECLARE fid VARCHAR(100) DEFAULT '';
DECLARE str VARCHAR(1000) DEFAULT rootId;

WHILE rootId != 0 DO
	set fid = (select parent_id from goo_class where id = rootId);
IF fid != 0 THEN
	set str = CONCAT(str,",",fid);
	set rootId = fid;
ELSE
	set rootId = fid;
end if;
end while;
	return str;
end 