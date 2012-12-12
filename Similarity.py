import nltk,pprint,re,fnmatch,os,sys
from nltk.corpus import wordnet as wn
from nltk.corpus import stopwords 
import operator,math
from os import listdir
from os.path import isfile, join
import itertools,glob
import xml.dom.minidom
from xml.dom.minidom import parseString

def LocateFiles():

    try:
        root=sys.argv[1]
        ListOfFiles=glob.glob(str(root)+'/*')
        return ListOfFiles
    except :
        print '''Please Input the Directory Name to read in the Format(Windows): C:\\Python26\\GESearch\\TaggedCorpus'''

    return None        

def POSTaggedFileReader():
  
  FileList=LocateFiles()
  TotalFiles=len(FileList)
  
  for file in FileList :
        files=open(file,'r').readlines()
        ConvertToNLTK(files)
    

def ConvertToNLTK(Lines):
       global positiveScore
       global negativeScore
       global neutralScore
           
       for line in Lines:
            positiveScore=0.0
            negativeScore=0.0
            neutralScore=0.0
            WordTagsList=[]
            line=line.strip()
            WordTags=line.split(' ')
            for word in WordTags:
                temp=tuple(word.split('_'))
                WordTagsList.append(temp)
            Chunker(WordTagsList)
			
            

def writeScore(taggedLine):
       global positiveScore
       global negativeScore
       global neutralScore
       fwrite=open("C:/Python26/tagged/Output/output.txt","a+")
       
       line=[]
       for word in taggedLine:
          line.append(word[0])
       line=" ".join(line)       
       fwrite.write(line+"|"+str(positiveScore)+"|"+str(negativeScore)+"|"+str(neutralScore)+"\n")
       fwrite.close()


def Grammar():
  
    grammar = r"""
 NP: {<DT|JJ|NNS|NN>+}          # Chunk sequences of DT, JJ, NN
 PP: {<IN><NP>}               # Chunk prepositions followed by NP
 VP: {<VB.*><NP|PP|CLAUSE>+} # Chunk verbs and their arguments
 CLAUSE: {<NP><VP>}  # Chunk NP, VP
 """
    Parser=nltk.RegexpParser(grammar)
    return Parser


def Chunker(WordTags):
           
           try: 
                         
                         adjectives(WordTags)
                         verb(WordTags)
                         writeScore(WordTags)

           except:
               raise
                         
def adjectives(Chunked):
        global positiveScore
        global negativeScore
        global neutralScore
           
        Adjective=[]
        
        for word in Chunked:
            W=re.match(r'JJ.?',str(word[1]))
            if W:
                    Adjective.append(word[0])
        for J in Adjective:
          J=J.strip()  
          J=J.lower()
          if J.isalpha():
             pos,neg,neu=similarityCalculation(J)
             if pos:
                         	positiveScore+=pos
             if neg:
                         	negativeScore+=neg
             if neu:
                         	neutralScore+=neu

def verb(Chunked):
        global positiveScore
        global negativeScore
        global neutralScore
           
        Verb=[]
        for word in Chunked:
           V=re.match(r'VB.?',str(word[1]))
           if V:
             Verb.append(word[0])
        for V in Verb:
          V=V.lower()
          V=V.strip()
          if V.isalpha():
             pos,neg,neu=similarityCalculation(V)
             if pos:
                         	positiveScore+=pos
                    
             if neg:
                         	negativeScore+=neg
             if neu:
                         	neutralScore+=neu
                
def positiveSynset():
   global positiveDict
   positiveDict={}
   fread=open("C:/Python26/tagged/Positive.txt","r").readlines()
   for words in fread:
      if words!=" " or len(words)>0:
         wrds=re.split(r',',str(words))
         for word in wrds:
				word=word.strip()
				synlist=wn.synsets(word)
				
				if len(synlist)>0:
					for syns in synlist:
						positiveDict[syns]=1

def negativeSynset():
   global negativeDict
   negativeDict={}
   fread=open("C:/Python26/tagged/newNegative.txt","r").readlines()
   for words in fread:
			word=words.strip()
			synlist=wn.synsets(word)
			if len(synlist)>0:
					for syns in synlist:
						negativeDict[syns]=1                         
def neutralSynset():
   global neutralDict
   neutralDict={}
   fread=open("C:/Python26/tagged/Neutral.txt","r").readlines()
   for words in fread:
         wrds=re.split(r',',str(words))
         for word in wrds:
				word=word.strip()
				synlist=wn.synsets(word)
				
				if len(synlist)>0:
					for syns in synlist:
						neutralDict[syns]=1                         
   
def similarityCalculation(word):
    global neutralDict
    global negativeDict
    global positiveDict
    neutralMax=0.0
    positiveMax=0.0
    negativeMax=0.0
    wordSynset=wn.synsets(word)
    if len(wordSynset)>0:
      try:
    	for neutral in neutralDict:
      		for word in wordSynset:   
        		similarity=str(neutral.path_similarity(word))
				
                        if similarity!="None":
                        	similarity=float(similarity)
                                if similarity>neutralMax:            
                                    neutralMax=similarity
                                   
        for negative in negativeDict:
      		for word in wordSynset:   
        		similarity=str(negative.path_similarity(word))
                        if similarity!="None":
                        	similarity=float(similarity)

                                if similarity>negativeMax:            
                                    negativeMax=similarity
        for positive in positiveDict:
      		for word in wordSynset:   
        		similarity=str(positive.path_similarity(word))
                        if similarity!="None":
                        	similarity=float(similarity)
                                if similarity>positiveMax:            
                                    positiveMax=similarity

        return positiveMax,negativeMax,neutralMax
      except:
      	raise
    return 0.0,0.0,0.0



 



def main():
    
    positiveSynset()
    negativeSynset()
    neutralSynset()
    POSTaggedFileReader()

if __name__=="__main__":
    main() 



