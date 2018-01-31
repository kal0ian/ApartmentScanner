import os.path

def get_words_from_files(dictionary):
	current_file_path = "E:\\ApartmentScanner\\apartment"
	words_set = set()
	for i in range(0, 2000):
		if(os.path.isfile(current_file_path+str(i))):
			with open(current_file_path+str(i)) as fr:
				lines = fr.readlines()
				lines = [x.strip() for x in lines]
				for line in lines:
					words = line.split(' ')
					for word in words:
						word.strip(",.")
						if word in dictionary:
							words_set.add(word)
	return sorted(words_set)			
					
def get_bulgarian_dictionary():
	official_dictionary_path = "E:\\bg_BG.dic"
	with open(official_dictionary_path) as dictionary:
		content = dictionary.readlines();
		content = [x.strip() for x in content]
	return content
		
def main():
	official_dict = get_bulgarian_dictionary()
	all_words = list(get_words_from_files(official_dict))	
	custom_dictionary_path = "E:\\bg_apartment_dictionary"
	with open(custom_dictionary_path, 'a+') as my_dict:
		for word in all_words:
			my_dict.write(word+"\n")

if __name__ == '__main__':
	main()