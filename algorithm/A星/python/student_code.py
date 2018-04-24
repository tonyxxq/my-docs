import math
from queue import PriorityQueue as PQueue

class node(object):
    def __init__(self, parent, index, g, h):
        self.parent = parent
        self.index = index
        self.g = g
        self.h = h
    
    def setParent(self, parent):
        self.parent = parent
    
    def setG(self, g):
        self.g = g
    
    def setH(self, h):
        self.h = h

def shortest_path(M, start, goal):
    open_queue = PQueue()
    open_dict = {}
    close_dict = {}
    start_h = cal_dist(M.intersections[start], M.intersections[goal])
    start_node = node(None, start, 0, start_h)
    open_queue.put((start_node.h + start_node.g, start_node))
    open_dict[start] = start_node
    # loop获取路径
    while goal not in close_dict and not open_queue.empty():
        find_path(M, open_queue, open_dict, close_dict, goal)
    # 找到路径
    path = []
    if goal in close_dict:
        current_node = close_dict[goal]
        while current_node.parent != None:
            path.append(current_node.index)
            current_node = current_node.parent
        path.append(start)
        path = [path[len(path) - i -1] for i in range(len(path))]
    return path

def find_path(M, open_queue, open_dict, close_dict, goal):
    # 从open_node找出一个点使得f值最小
    current_node = open_queue.get()[1]
    open_dict.pop(current_node.index)
    # 找到该点附近的点
    roads = M.roads[current_node.index]
    update_node = False
    for i in roads:
        # 遍历过的结点不用再次遍历 
        if i not in close_dict:
            distance = cal_dist(M.intersections[i], M.intersections[current_node.index])
            new_g = current_node.g + distance
            if i in open_dict:
                if new_g < open_dict[i].g:
                    open_dict[i].parent = current_node
                    open_dict[i].g = new_g
                    update_node = True
            else:
                new_h = cal_dist(M.intersections[i], M.intersections[goal])
                new_node = node(current_node, i, new_g, new_h)
                open_dict[i] = new_node
                open_queue.put((new_node.h + new_node.g, new_node))
    # 如果修改了结点的内容需要新建队列
    if update_node:
        open_queue = PQueue()
        for v in open_dict.values():
            open_queue.put((v.h + v.g, v))
    # 把当前点放入关闭列表，表示已经访问过
    close_dict[current_node.index] = current_node
    
# 计算两点之间的欧式距离
def cal_dist(p1, p2):
    return math.sqrt((p1[0] - p2[0])**2 + (p1[1]- p2[1])**2)