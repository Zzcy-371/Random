export const CATEGORY_MAP: Record<number, string> = {
  1: 'eating',
  2: 'drinking',
  3: 'playing',
  4: 'staying',
  5: 'other',
};

export const CATEGORY_SLUG_TO_ID: Record<string, number> = {
  eating: 1,
  drinking: 2,
  playing: 3,
  staying: 4,
  other: 5,
};

export const CATEGORY_DISPLAY_NAMES: Record<string, string> = {
  eating: '吃',
  drinking: '喝',
  playing: '玩',
  staying: '住',
  other: '其他',
};

export const CATEGORY_ICONS: Record<string, string> = {
  eating: 'utensils',
  drinking: 'coffee',
  playing: 'gamepad',
  staying: 'bed',
  other: 'shuffle',
};

export const MODULE_TAGS: Record<string, string[]> = {
  eating: ['健康', '素食', '辣', '清淡', '快餐', '精致', '亚洲', '西式', '低卡'],
  drinking: ['茶', '咖啡', '果汁', '热饮', '冷饮', '含咖啡因', '无酒精'],
  playing: ['室内', '户外', '运动', '休闲', '单人', '多人', '游戏', '社交'],
  staying: ['经济', '中档', '豪华', '酒店', '民宿', '市中心', '安静', '风景'],
  other: [],
};
