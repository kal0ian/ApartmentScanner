from bs4 import BeautifulSoup
import requests
import os.path

def getNextPage():
	pageBase = "https://www.imot.bg/pcgi/imot.cgi?act=3&slink=3f2cvk&f1="
	linkList = list()
	for x in range(1,26):
		try:
			page = requests.get(pageBase+str(x))
			crawlPageAndGetLink(page, linkList)
		except Exception as e:
			print("Exception at page "+str(x))
			continue
	getAdsDetails(linkList)


def crawlPageAndGetLink(page, linkList):
	soup = BeautifulSoup(page.content, 'html.parser')
	ads = soup.find_all('td', valign = 'top', width = '270', height='40', style="padding-left:4px")
	links=list()
	for item in ads:
		links.extend(item.find_all('a', class_="lnk1"))
	for a in links:
		linkList.append('https:'+a['href'])

def getAdsDetails(linkList):
	for i in range(1000,1000+len(linkList)):
		try:
			saveAdToFile(linkList[i-1000], i)
		except IndexError:
			print('file'+str(i))
			continue
		except UnicodeEncodeError:
			print('fileunicode'+str(i))
			if(os.path.isfile('E:\\ApartmentScanner\\apartment'+str(i))) :
				os.remove('E:\\ApartmentScanner\\apartment'+str(i))
			continue
		except Exception as e:
			print('Exception at file'+str(i))
			continue

def saveAdToFile(url, i):
	filePath = "E:\\ApartmentScanner\\"
	fileName = "apartment"
	page = requests.get(url);
	bs = BeautifulSoup(page.content, 'html.parser')
	title = getAdTitle(bs)
	price = getPrice(bs)
	description = getDescription(bs)
	location = getLocation(bs)
	mainInfo = getMainInfo(bs)
	area = mainInfo[0]
	onFloor = mainInfo[1]
	constructionType = mainInfo[2]
	phoneNum = getPhoneNum(bs)
	with open(filePath+fileName+str(i), 'a') as currFile:
		currFile.write(title+'\n')
		currFile.write(price+'\n')
		currFile.write(description+'\n')
		currFile.write(location+'\n')
		currFile.write(area+'\n')
		currFile.write(onFloor+'\n')
		currFile.write(constructionType+'\n')
		currFile.write(phoneNum+'\n')
		currFile.write(url+'\n')

def getAdTitle(bs):
	return bs.find_all('div', class_='name')[0].get_text().strip()

def getPrice(bs):
	return bs.find_all('div', class_='pricePhoto')[0].get_text().strip()

def getDescription(bs):
	return bs.find_all('div', id='description_div')[0].get_text().replace("Виж по-малко... Виж повече", "").strip()

def getLocation(bs):
	return bs.find_all('span', style="font-size:16px; font-weight:bold;")[0].get_text().strip()

def getMainInfo(bs):
	l = bs.find_all('table', width='344', cellspacing='0', cellpadding='0', border='0', style="margin-top:7px;")[0].find_all('td')
	resultList = list()
	resultList.append(l[1].get_text().strip())
	resultList.append(l[4].get_text().strip())
	resultList.append(l[10].get_text().strip())
	return resultList

def getPhoneNum(bs):
	return bs.find_all('span', style="font-size:22px; font-weight:bold; display:inline-block; vertical-align:-4px;")[0].get_text().strip()


def main():
	getNextPage()

if __name__ == '__main__':
	main()
