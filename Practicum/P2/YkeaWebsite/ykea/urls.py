from django.conf.urls import url

from . import views


listOfAddresses = ["sd2018-ykeaf4.herokuapp.com"]

urlpatterns = [
	url(r'^$', views.index, name='index'),
	url(r'^items/$', views.items, name='items'),
    url(r'^items/(?P<category>.*)/$', views.items, name='items'),

    url(r'^item/(?P<item_number>.*)/$', views.findItem, name='details'),
    url(r'^shoppingcart/$', views.shoppingcart, name='shoppingcart'),
    url(r'^buy/$', views.buy, name='buy'),


    url(r'^register/$', views.register, name='register'),
    url(r'^comparator/$', views.comparator, {'ips': listOfAddresses}, name='comparator')


]
