# -*- coding: utf-8 -*-
# Generated by Django 1.11.12 on 2018-05-22 10:02
from __future__ import unicode_literals

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('ykea', '0003_auto_20180512_0907'),
    ]

    operations = [
        migrations.CreateModel(
            name='Client',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('saldo', models.FloatField(default=500)),
                ('carrito', models.OneToOneField(null=True, on_delete=django.db.models.deletion.CASCADE, to='ykea.Shoppingcart')),
                ('user', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, to=settings.AUTH_USER_MODEL)),
            ],
        ),
    ]