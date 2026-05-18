export interface Documento {
    id: string;
    conteudo: string;
    metadata: {
        topico?: string;
        fonte?: string;
        nivel_material?: string;
        source?: string;
    } | string; 
}