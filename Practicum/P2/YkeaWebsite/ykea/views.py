# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render

from django.http import HttpResponse, HttpResponseRedirect
from .models import Item
from .models import Client
from .models import Shoppingcart
from .models import N_items
from .models import User
from django.contrib import auth
from django.core.urlresolvers import reverse
from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.http import HttpResponseRedirect
from django.shortcuts import render
from django.contrib.auth.decorators import login_required
from django.contrib.auth.decorators import user_passes_test
from django.contrib.auth.models import User
from rest_framework import viewsets
from .serializers import ItemSerializer



# Create your views here.
def index(request):
    #Shoppingcart.objects.all().delete()
    categories = list()
    for i in Item.CATEGORIES:
        categories.append(i[0])

    if request.user.is_authenticated:
        usuari  = Client.objects.get(user = request.user.id)
        context = {
            'category': categories,
            'usuari' : usuari
        }
    else:
        context = {
            'category': categories,
            
        }

    if request.POST.get("refresh"):
        return HttpResponseRedirect('/ykea/')
    if request.POST.get("log"):
        username = request.POST.get('username', '')
        password = request.POST.get('password', '')

        user = auth.authenticate(username=username, password=password)


        if user is not None and user.is_active:
            # Correct password, and the user is marked "active"
            auth.login(request, user)
            # Redirect to a success page.
            return HttpResponseRedirect('/ykea/')
        else:
            # Show an error page
            return HttpResponse("Invalid username or password")
    elif request.POST.get("noLog"):
        return HttpResponseRedirect("../accounts/logout/")

    return render(request, 'ykea/mainPage.html', context)

def items(request, category=""):
    items_by_category = Item.objects.filter(category=category)
    context = {
        'items': items_by_category,
        'category': category,
    }
    path = request.get_full_path()
    if request.POST.get("log"):
        username = request.POST.get('username', '')
        password = request.POST.get('password', '')

        user = auth.authenticate(username=username, password=password)


        if user is not None and user.is_active:
            # Correct password, and the user is marked "active"
            auth.login(request, user)
            # Redirect to a success page.
            return HttpResponseRedirect(path)
        else:
            # Show an error page
            return HttpResponse("Invalid username or password")
    elif request.POST.get("noLog"):
        return HttpResponseRedirect("https://sd2018-ykeaf4.herokuapp.com/accounts/logout/")
    return render(request, 'ykea/items.html', context)

def findItem(request,item_number=""):
    path = request.get_full_path()
    if request.POST.get("log"):
        username = request.POST.get('username', '')
        password = request.POST.get('password', '')

        user = auth.authenticate(username=username, password=password)


        if user is not None and user.is_active:
            # Correct password, and the user is marked "active"
            auth.login(request, user)
            # Redirect to a success page.
            return HttpResponseRedirect(path)
        else:
            # Show an error page
            return HttpResponse("Invalid username or password")
    elif request.POST.get("noLog"):
        return HttpResponseRedirect("https://sd2018-ykeaf4.herokuapp.com/accounts/logout/")
    try:
        item = Item.objects.filter(item_number=item_number)
        if not item:
            raise ItemNotFound
        context = {
            'numItem': item
        }
        return render(request, 'ykea/itemInfo.html',context)
    except ItemNotFound:
        return HttpResponse("I'm afraid we couldn't find the product requested!")

@login_required
def shoppingcart(request):
    #Si carrito
    usuari  = Client.objects.get(user = request.user.id)
    if usuari.carrito == None:
        carrito = Shoppingcart.objects.create(user = usuari)
        usuari.carrito = carrito
        usuari.carrito.save()
    else:
        carrito = usuari.carrito

    present = False

    for key in request.POST:
        if key.startswith("checkbox"):
            #Si item existeix, aumentar quantitat
            for numeroItem in usuari.carrito.items.all():
                if numeroItem.item.item_number==request.POST[key]:
                    present = True
                    numeroItem.qty += 1
                    numeroItem.save()
            if not present:
                item = N_items(item = Item.objects.get(item_number=request.POST[key]))
                item.save()
                usuari.carrito.items.add(item)
                usuari.carrito.save()
    return HttpResponseRedirect(reverse('buy'))

@login_required
def buy(request):

    noMoney = False
    usuari = Client.objects.get(user=request.user.id)
    sc = usuari.carrito
    item_numbers = []
    item_seleccionats_comprar = []
    
    for key in request.POST:
        if key.startswith("IntegerField"):
            for i in sc.items.all():
                    if i.item.item_number == key[12:]:
                        i.qty = request.POST[key]
                        i.save()

    if request.POST.get("clicaDelete"):

        for key in request.POST:
            
            if key.startswith("checkbox"):
                for i in sc.items.all():
                    if i.item.item_number == request.POST[key]:
                        if i.qty > 1:    
                            i.qty -= 1
                            i.save()
                        else:
                            i.delete()
    elif request.POST.get("clicaCheck"):
        total = 0
        for key in request.POST:
            
            if key.startswith("checkbox"):
                for i in sc.items.all():
                    if i.item.item_number == request.POST[key]:
                        item_seleccionats_comprar.append(i)
            if key.startswith("IntegerField"):
                for i in sc.items.all():
                    if i.item.item_number == key[12:]:
                        i.qty = request.POST[key]
                        i.save()
                        
        for numeroI in sc.items.all():
            if numeroI in item_seleccionats_comprar:
                item_numbers.append(numeroI)
                total += numeroI.qty * numeroI.item.price

        usuari.carrito.pastaCarrito = total
        dinersRestants = float(usuari.saldo) - float(total)

        if dinersRestants < 0.0:
            noMoney = True

            
        context = {
            'items_by_number': item_numbers,
            'preu': total,
            'moneyUser' : usuari.saldo,
            'resta' : dinersRestants,
            'noMoney' : noMoney
        }    

        #Buidar
        #usuari.carrito.delete()
        
        for itemC in sc.items.all():
            itemC.delete()
        sc.pastaCarrito = 0


        return render(request, 'ykea/checkout.html',context)

    elif request.POST.get("comprar"):
        pagar = request.POST.get('diners', '')

        Client.objects.filter(user=request.user.id).update(saldo=pagar)

        
        return HttpResponseRedirect('/ykea/')


    for numeroI in sc.items.all():

        item_numbers.append(numeroI)

    context = {
        'items_by_number':item_numbers
    }

    return render(request, 'ykea/shoppingcart.html',context)


@login_required
def logout_view(request):

    auth.logout(request)
    # Redirect to a success page.
    return HttpResponseRedirect("/account/loggedout/")

def user_not_registred(user):
    return not user.is_authenticated() 

@user_passes_test(user_not_registred, login_url="/ykea")
def register(request):

    if request.method == 'POST':
        form = UserCreationForm(request.POST)
        if form.is_valid():
            new_user= form.save()
            cart = Shoppingcart.objects.create(user=new_user)
            Client.objects.create(user=new_user, carrito=cart)
            return HttpResponseRedirect("/ykea")
    else:
        form = UserCreationForm()
    return render(request, "registration/register.html", {
        'form': form,
    })

def comparator(request, ips):
    context = {
        'ips':ips
    }
    return render(request, 'ykea/comparator.html', context)       

def user_is_commercial(user):

    return (user.groups.filter(name='Commercial users').exists() or user.is_staff)

def user_is_admin(user):
    return user.is_staff 


#@user_passes_test(user_is_commercial, login_url="http://localhost:8000/ykea/")

class ItemNotFound():
	#Aquesta clase s'encarrega del error que sorgeix
	#quan l'usuari entra un numero d'un item no trobat.

	pass
    
class ItemViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows Items to be viewed or edited.
    """
    
    serializer_class = ItemSerializer

    def get_queryset(self):
        queryset = Item.objects.all().order_by('item_number')

        #Category
        category = self.request.query_params.get("category", None)
        nou = self.request.query_params.get("new",None)
        preu = self.request.query_params.get("price", None)

        if category is not None:
            queryset = queryset.filter(category = category)

        #is_new attribute
        

        if nou is not None:
            if nou == "yes" or nou == "True":
                queryset = queryset.filter(is_new = True)
            else:
                queryset = queryset.filter(is_new = False)

        #preu

        
        if preu is not None:
            queryset = queryset.filter(price__range=(0,preu))
        return queryset