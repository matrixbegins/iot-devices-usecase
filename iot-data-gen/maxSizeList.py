import random

class MaxSizeList(object):

    def __init__(self, max_length):
        self.max_length = max_length
        self.ls = []

    def push(self, st):
        if len(self.ls) == self.max_length:
            self.ls.pop(0)
        self.ls.append(st)

    def get_list(self):
        return self.ls

    def get_length(self):
        return len(self.ls)

    def getRandomElement(self):
        idx = random.choice(range(len(self.ls)))
        elm = self.ls[idx]
        self.ls.remove(elm)
        return elm

