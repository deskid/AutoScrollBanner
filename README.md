## AutoScrollBanner

### 1. 实现思路一

1. ViewPager + ImageView 
2. Int.MAX_VALUE % size
3. optimize viewpager populate()


### 2. 实现思路二 

//todo

1. ViewPager + ImageView
2. (size + 2) % size
3. realPosition = (position - 1) % size 

