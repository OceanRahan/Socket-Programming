# Socket-Programming
Java Socket Programming 
This was given as assignment (Offline) in our Networking sessional. The system has the following features:
# It establishes connection with clients. 
# Each client has a unique ID (We used our student ids).
# To establish connection with the server one has to provide student id. 
# If the student is already connected to the server then the connection is not accepted and the thread terminates. 
# It is a multithread program i.e, can accept more than one client. According to my code at most 120 clients could be connected at the same time.
# The main task was to send a file to another client. 

Client.class maintains client end
CilentConnected.class builds connectection between client and server and all the information exchange is communicated through this class
server.class is for server end commmunication.
