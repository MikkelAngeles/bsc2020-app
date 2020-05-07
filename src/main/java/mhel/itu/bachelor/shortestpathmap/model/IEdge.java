package mhel.itu.bachelor.shortestpathmap.model;

public interface IEdge {
    int index();
    IVertex from();
    IVertex to();
}
