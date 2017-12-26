#!/usr/bin/env python
# coding=utf-8
# ConnectionPool.py

__author__ = 'Jun Zhang'

import redis
from redis.client import Redis

pool = redis.BlockingConnectionPool(host = '10.77.2.21', port = 20170)

def getConnection():
	return pool

