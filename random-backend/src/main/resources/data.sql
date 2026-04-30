INSERT INTO categories (name, display_name, icon, sort_order, created_at)
SELECT 'eating', '吃', 'utensils', 1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'eating');

INSERT INTO categories (name, display_name, icon, sort_order, created_at)
SELECT 'drinking', '喝', 'coffee', 2, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'drinking');

INSERT INTO categories (name, display_name, icon, sort_order, created_at)
SELECT 'playing', '玩', 'gamepad', 3, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'playing');

INSERT INTO categories (name, display_name, icon, sort_order, created_at)
SELECT 'staying', '住', 'bed', 4, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'staying');

INSERT INTO categories (name, display_name, icon, sort_order, created_at)
SELECT 'other', '其他', 'shuffle', 5, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'other');
