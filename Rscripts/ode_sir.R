# SIR ODE models using desolve
# S. J. Lycett
# 7 April 2014

# see http://cran.r-project.org/web/packages/deSolve/vignettes/deSolve.pdf

library(deSolve)


###########################################################
# define SIR function for ODEs

SIR <- function(t, state, parameters) {
	with( as.list(c(state, parameters)),
		{
 			# rate of change
 			dS <- -b*S*I
	 		dI <- b*S*I - y*I
 			dR <- y*I
	
			# return the rate of change
 			list( c(dS, dI, dR) )
 		}
	)
}

############################################################

doSIRExample <- FALSE

if (doSIRExample) {

	# define parameters (order is important)
	parameters <- c(b=0.1/1000, y=0.05)
	state      <- c(S=999, I=1, R=0)
	times      <- seq(0, 1000, by = 1)


	# run a model
	res <- ode(y = state, times = times, func = SIR, parms = parameters)



	path <- "C://Users//Samantha Lycett//Documents//NetBeansProjects//compare_with_r//"
	name <- "Rgenerated_SIR_with_ODE"

	# plot model
	mainTxt <- paste("N=1000","b=",parameters[1],"y=",parameters[2])
	plot(res[,1], res[,2], type="l", ylim=c(0,1000), xlab="Time", ylab="Number", main=mainTxt, col="blue")
	lines(res[,1], res[,3], col="red")
	lines(res[,1], res[,4], col="black")

	png( file=paste(path,name,".png",sep=""), height=1800, width=1800, res=300)
	mainTxt <- paste("N=1000","b=",parameters[1],"y=",parameters[2])
	plot(res[,1], res[,2], type="l", ylim=c(0,1000), xlab="Time", ylab="Number", main=mainTxt, col="blue")
	lines(res[,1], res[,3], col="red")
	lines(res[,1], res[,4], col="black")
	dev.off()

	fname<- paste(path,name,".csv",sep="")
	write.table(res, file=fname, sep=",", col.names=TRUE, row.names=FALSE)

}

