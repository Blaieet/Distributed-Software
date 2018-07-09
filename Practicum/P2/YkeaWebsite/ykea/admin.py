# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.contrib import admin
from .models import Item
from .models import Client
from .models import Shoppingcart

# Register your models here.

admin.site.register(Item)
admin.site.register(Client)
admin.site.register(Shoppingcart)
