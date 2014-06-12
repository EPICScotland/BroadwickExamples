# Custom R functions to plot BroadwickExamples output on GB map
# S. J. Lycett
# 10 June 2014

# load required libraries for map plotting

library(maps)
library(mapdata)
library(mapproj)


#########################################################################################
# function to map GB map with locations marked
# assume locations has 4 columns: LocationName, Latitude, Longitude and State (S,E,I,R,M)
# colour of locations depends on State
# S = transparent, E = orange, I = red, R = blue, M = green

plotGBDiseaseMap <- function( locations=locations, links=links, 
						stateNames=c("SUSCEPTIBLE","EXPOSED","INFECTED","RECOVERED","IMMUNE"),
						stateCols =c(NA, "orange", "red", "blue", "green"),
						onlyPoints=FALSE, showLinks=TRUE, showLegend=!onlyPoints,
						return.coords=FALSE, title="" ) {

	fromLoc	 <- match(links[,2], locations$Location)
	toLoc		 <- match(links[,6], locations$Location)

	orientation <- c(90,0,0)
	projection="mercator"
	
	# plot the map
	if (!onlyPoints) {
		map(database= "worldHires", regions="UK:Great Britain", fill=FALSE, projection=projection, orientation=orientation)
	}

	# plot the city locations and the links
	# convert points to projected lat/long for plotting
	coord  	<- mapproject(locations$Longitude, locations$Latitude, projection=projection, orientation=orientation) 

	if ((showLinks) & (!onlyPoints)) {	
		# plot links
		for (i in 1:length(fromLoc)) {
			lines( coord$x[c(fromLoc[i],toLoc[i])], coord$y[c(fromLoc[i],toLoc[i])], col="black")
		}
	}

	# overlay cities
	# calculate city colours
	cityCols <- stateCols[match( locations$State, stateNames )]
	points(coord, bg=cityCols, pch=21) #plot converted points

	# overlay legend
	if (showLegend) {
		legend("topright", stateNames, pch=21, pt.bg=stateCols, bty="n")
		title(title)
	}

	if (return.coords) {
		return( coord )
	}
}


#####################################################################
# MAIN SCRIPT - EXAMPLE USEAGE

	# load locations with states and links

	# obviously change this path to be for your computer.
	path 			 <- "NetBeansProjects//BroadwickExamples//"

	name			 <- "individualnet_test"

	##################################################################################
	# load the original network, this does not change over time in this example anyway
	##################################################################################
	links			 <- read.table( paste(path,name,"_initialNetwork.net",sep=""), header=TRUE, sep=",")


	#####################
	# list files to plot
	#####################

	fnames <- dir(path)
	fnames <- fnames[grep( paste(name,"_individualStates_[0-9]+",sep=""), fnames)]
	fnames <- c( paste(name,"_individualStates_initial.txt",sep=""), 
			 fnames, 
			 paste(name,"_individualStates_final.txt",sep="") )

	
	for (i in 1:length(fnames)) {

		###########################################
		# load the infection state of the locations
		###########################################
		locationsWithState <- read.table( paste(path,fnames[i],sep=""), header=TRUE, sep=",")
	
		###############
		# plot the map
		###############
		plotGBDiseaseMap( locations=locationsWithState, links=links, showLinks=TRUE, title=fnames[i])

		# pause
		Sys.sleep(0.01)	
	}

	

