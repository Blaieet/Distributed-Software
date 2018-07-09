# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class Item(models.Model):
    CATEGORIES = (
        ("beds", "Beds & mattressess"),
        ("furn", "Furniture, wardrobes & shelves"),
        ("sofa", "Sofas & armchairs"),
        ("table", "Tables & chairs"),
        ("texti","Textiles"),
        ("deco","Decoration & mirrors"),
        ("light","Lighting"),
        ("cook","Cookware"),
        ("tablw","Tableware"),
        ("taps","Taps & sinks"),
        ("org", "Organisers & storage accesories"),
        ("toys","Toys"),
        ("leis","Leisure"),
        ("safe","safety"),
        ("diy", "Do-it-yourself"),
        ("floor","Flooring"),
        ("plant","Plants & gardering"),
        ("food","Food & beverages")
    )
    item_number = models.CharField(max_length=8, unique=True)
    name = models.CharField(max_length=50)
    description = models.TextField()
    price = models.DecimalField(max_digits=8, decimal_places=2)
    is_new = models.BooleanField()
    size = models.CharField(max_length=40)
    instructions = models.FileField(upload_to="ykea/static/instructions")
    featured_photo = models.ImageField(upload_to="ykea/static/images")
    category = models.CharField(max_length=5, choices=CATEGORIES)
    def __str__(self):
        return  ('[**NEW**]' if self.is_new else '') + "[" + self.category + "] [" + self.item_number + "] " + self.name + " - " + self.description + " (" + self.size + ") : " + str(self.price) + " €"

class N_items(models.Model):
    item = models.ForeignKey(Item, on_delete=models.CASCADE)
    qty = models.IntegerField(default =1)
    
    def _str_(self):
        return self.item.name+" "+"{0}".format(self.qty)

class Shoppingcart(models.Model):
    
    user = models.ForeignKey(User,null=True, on_delete = models.CASCADE)
    items = models.ManyToManyField(N_items)
    pastaCarrito = models.FloatField(default=0)

class Client(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    carrito = models.OneToOneField(Shoppingcart, null=True,on_delete=models.CASCADE)
    saldo = models.FloatField(default=2000)
    def __str__(self):
        return (self.user.username+" "+str(self.saldo))   