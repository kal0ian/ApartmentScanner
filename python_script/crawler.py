from bs4 import BeautifulSoup
import requests
import os.path

def getNextPage():
	pageBase = "http://www.imoti.com/pcgi/results.cgi?page="
	linkList = list()
	for x in range(1,51):
		page = requests.get(pageBase+str(x))
		crawlPageAndGetLink(page, linkList)
	getAdsDetails(linkList)


def crawlPageAndGetLink(page, linkList):
	links=list()
	pageBase = "http://www.imoti.com/pcgi/"
	soup = BeautifulSoup(page.content, 'html.parser')
	ads = soup.find_all('div', class_='item')
	ads = ads[5:]
	for item in ads:
		for a in item.find_all('a', href=True):
			linkList.append(pageBase+a['href'])


def getAdsDetails(linkList):
	for i in range(len(linkList)):
		try:
			saveAdToFile(linkList[i], i)
		except IndexError:
			print('file'+str(i))
			continue
		except UnicodeEncodeError:
			print('fileunicode'+str(i))
			if(os.path.isfile('E:\\ApartmentScanner\\apartment'+str(i))):
				os.remove('E:\\ApartmentScanner\\apartment'+str(i))
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
	h1 = bs.find_all('h1', class_='name')
	if(len(h1)>0):
		return h1[0].get_text().strip().replace('\n', ' ')

def getPrice(bs):
	return bs.find_all('div', class_='price')[0].get_text().strip().split('\n')[0].strip()
	
def getDescription(bs):
	info = bs.find_all('div', class_='info')[1]
	return info.get_text().split('\n')[2].strip()

def getLocation(bs):
	regex = 'Местоположение:'
	return bs.find_all('div',class_='location')[0].get_text().split(regex)[-1].strip()

def getMainInfo(bs):
	l = bs.find_all('div', class_='mainInfo')[0].find_all('span')
	resultList = list()
	resultList.append(l[0].get_text())
	resultList.append(l[1].get_text())
	resultList.append(l[4].get_text())
	return resultList

def getPhoneNum(bs):
	return bs.find_all('td', class_='info')[0].find_all('div')[1].get_text()


def main():
	getNextPage()

if __name__ == '__main__':
	main()
