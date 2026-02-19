-- Repeatable: Term status synchronization triggers
-- These are re-applied on every migration run to stay up to date.

-- Drop existing triggers first to allow re-creation
DROP TRIGGER IF EXISTS trig_words_update_status_children;
DROP TRIGGER IF EXISTS trig_words_update_status_parent;

-- When a parent word's status changes and sync is enabled, propagate to children
CREATE TRIGGER trig_words_update_status_children
AFTER UPDATE OF WoStatus ON words
WHEN NEW.WoSyncStatus = 1
BEGIN
  UPDATE words SET WoStatus = NEW.WoStatus, WoStatusChanged = DATETIME('now')
  WHERE WoID IN (SELECT WpWoID FROM wordparents WHERE WpParentWoID = NEW.WoID)
  AND WoStatus != NEW.WoStatus;
END;

-- When a child word's status changes, propagate to parent if parent has sync enabled
CREATE TRIGGER trig_words_update_status_parent
AFTER UPDATE OF WoStatus ON words
BEGIN
  UPDATE words SET WoStatus = NEW.WoStatus, WoStatusChanged = DATETIME('now')
  WHERE WoID IN (SELECT WpParentWoID FROM wordparents WHERE WpWoID = NEW.WoID)
  AND WoSyncStatus = 1
  AND WoStatus != NEW.WoStatus;
END;
