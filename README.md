Java Version 8 or above is recommended to run this code
https://www.java.com/en/download/manual.jsp

In order to run the program, it must be in the same folder as both of the files you would like to compare. see more about formatting those files below.

This program takes in 5 parameters: Name of Older File, Name of Newer File, Min Tokens for Consideration, Number of Tokens to Drop, and Format, where the files contain the wallet addresses and token amounts of a list of addresses.

the Names of the files must include the file type
EX: sampleOld.csv

min tokens and number of tokens must both be positive integers or
zero

to select the format simply type Token (case insensitive) or NFT (also case insensitive). only press submit when all parts have been filled out.

to exclude a wallet from the calculations, put its address into the 'Wallet to Exclude' tab and press 'Exclude' this will exclude it from both the total number of tokens held calculation, and the rewards calculation. any number of wallets can be excluded, just press 'Exclude' each time. Currently, the only way to re-include a wallet is by restarting the program.

it then adds up the total amount of tokens wallets in the older file had, only including wallets that have at least the same number of tokens as they did in the old flie in the newer file, and then distributes the number of tokens to drop based on what percentage of that total each wallet has

EXAMPLE:	old	new
Wallet A 	20   -> 17
Wallet B	90   -> 90
Wallet C	10   -> 400
Wallet D	5    -> 10

Tokens to drop: 10

Minimum tokens for consideration: 10

Wallets included in total: B and C

total held: 100 tokens

amount to distribute:
Wallet B, 9 tokens
Wallet C, 1 token



this code requires two input files to run, a newer file and
and older file. 

it accepts two formats called token and nft, they cannot be mixed and matched.

The Token format requires the information in a .csv or .txt file
oragnized like this:

"HolderAddress","Balance","PendingBalanceUpdate"

the quotes around each value are necessary, and the first line
must be the above template with no real information. This is the way it is formatted when downlaoding holder speadsheets from etherscan.

The NFT format requires the information in a .csv or .txt file
organized like this:

LineNumber,OpenseaAccountNickname,NFTCreator,NumberOwned,WalletAddress

the LineNumber, OpenseaAccountNickName, and NFTCreator do not matter, as long as some text is put there its contents are unimportant. they are there because that is how our program got the data off of opensea. no quotes should be added.