# -*- coding: utf-8 -*-
# Generated by Django 1.11.12 on 2018-05-24 08:58
from __future__ import unicode_literals

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('ykea', '0007_shoppingcart_user'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='shoppingcart',
            name='idCarrito',
        ),
    ]
