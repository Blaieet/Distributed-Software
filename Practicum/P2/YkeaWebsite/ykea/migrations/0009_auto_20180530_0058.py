# -*- coding: utf-8 -*-
# Generated by Django 1.11.12 on 2018-05-30 00:58
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('ykea', '0008_remove_shoppingcart_idcarrito'),
    ]

    operations = [
        migrations.AlterField(
            model_name='item',
            name='featured_photo',
            field=models.ImageField(upload_to='ykea/static/images'),
        ),
        migrations.AlterField(
            model_name='item',
            name='instructions',
            field=models.FileField(upload_to='ykea/static/instructions'),
        ),
    ]
